#!/bin/bash

#需要将当前登录用户，如dss加入到sudoers

#函数
check_principal_exist(){
    all_principal=`timeout 30 sshpass -p $PASSWORD ssh -p $SSH_PORT $KDC_USER@$KDCSERVER "sudo /usr/sbin/kadmin.local -q \"list_principals\""`    #echo "all_principal:"$all_principal
    principal=$1
    if [[ $all_principal =~ $principal ]]
    then
        #echo "包含"
        return 1
    else
        #echo "不包含"
        return 0
    fi
}

add_principal(){
    principalPrefix=$1
    echo "add_principal func,principalPrefix:"$principalPrefix
    check_principal_exist "$principalPrefix@$REALM"
    ifexist=$?
    if [ $ifexist -eq 1 ]
    then
        echo "已有principal"
    else
        echo "没有principal,将会生成"
        timeout 30 sshpass -p $PASSWORD ssh -p $SSH_PORT $KDC_USER@$KDCSERVER "sudo /usr/sbin/kadmin.local -q \"addprinc -randkey $principalPrefix\""
    fi
}

gen_keytab(){
    user=$1
    host=$2
    principalPrefix="$user/$host"
    principal="$user/$host@$REALM"
    add_principal $principalPrefix
    timeout 30 sshpass -p $PASSWORD ssh -p $SSH_PORT $KDC_USER@$KDCSERVER "sudo rm -rf /tmp/$host.$user.keytab"
    timeout 30 sshpass -p $PASSWORD ssh -p $SSH_PORT $KDC_USER@$KDCSERVER "sudo /usr/sbin/kadmin.local -q \"xst -norandkey -k  /tmp/$host.$user.keytab $user/$host\""
    timeout 30 sshpass -p $PASSWORD ssh -p $SSH_PORT $KDC_USER@$KDCSERVER "sudo chmod 755 /tmp/$host.$user.keytab"
    timeout 30 sshpass -p $PASSWORD scp -P $SSH_PORT $KDC_USER@$KDCSERVER:/tmp/$host.$user.keytab $CENTER_KEYTAB_PATH
    if [[  -f "$host.$user.keytab" ]]; then
       mv $CENTER_KEYTAB_PATH/$host.$user.keytab $CENTER_KEYTAB_PATH/$user.keytab
       if [[ $? != 0 ]];then
           echo "rename keytab failed!"
       else
           kinit -kt $CENTER_KEYTAB_PATH/$user.keytab $principal
           sudo su - $user -c "crontab -l > conf && echo '* */12 * * *  kinit -kt $CENTER_KEYTAB_PATH/$user.keytab $principal' >> conf && crontab conf && rm -f conf"
       fi
    else
       echo "the $user.keytab does not exist, please check your previous steps!"
    fi
}

gen_keytab_user(){
    user=$1
    principalPrefix="$user"
    principal="$user@$REALM"
    add_principal $principalPrefix
    timeout 30 sshpass -p $PASSWORD ssh -p $SSH_PORT $KDC_USER@$KDCSERVER "sudo rm -rf /tmp/$user.keytab"
    timeout 30 sshpass -p $PASSWORD ssh -p $SSH_PORT $KDC_USER@$KDCSERVER "sudo /usr/sbin/kadmin.local -q \"xst -norandkey -k  /tmp/$user.keytab $user\""
    timeout 30 sshpass -p $PASSWORD ssh -p $SSH_PORT $KDC_USER@$KDCSERVER "sudo chmod 755 /tmp/$user.keytab"
    timeout 30 sshpass -p $PASSWORD scp -P $SSH_PORT $KDC_USER@$KDCSERVER:/tmp/$user.keytab $CENTER_KEYTAB_PATH
    if [[  -f "$user.keytab" ]]; then
        kinit -kt $CENTER_KEYTAB_PATH/$user.keytab $principal
        sudo su - op -c "crontab -l > conf && echo '* */12 * * *  kinit -kt $CENTER_KEYTAB_PATH/$user.keytab $principal' >> conf && crontab conf && rm -f conf"
    else
        echo "the $user.keytab does not exist, please check your previous steps!"
    fi

}



#第一个参数为功能参数（必须有）,第二个为user（必须有），第三个为host（可以有）
if [ $# -lt 2 ] || [ $# -gt 8 ]; then
    echo -e "\033[31m \033[05m请确认您的操作,输入格式如下 功能参数 [user|user hostname]\033[0m"
    echo "Usage: $0 genenateKeytab {username|username hostname}"
    echo `date '+%Y-%m-%d %H:%M:%S'`" parameters:"$* >>/tmp/deltaKerberos.log
    exit 1
else
    if [ $# -eq 7 ]; then
        user=$1
        CENTER_KEYTAB_PATH=$2
        SSH_PORT=$3
        KDCSERVER=$4
        KDC_USER=$5
        PASSWORD=$6
        REALM=$7
        echo `date '+%Y-%m-%d %H:%M:%S'`" in genenate_key_tab username:"$user >>/tmp/deltaKerberos.log
        gen_keytab_user $user
    else
        user=$1
        host=$2
        CENTER_KEYTAB_PATH=$3
        SSH_PORT=$4
        KDCSERVER=$5
        KDC_USER=$6
        PASSWORD=$7
        REALM=$8
        echo `date '+%Y-%m-%d %H:%M:%S'`" in genenate_key_tab username:"$user" hostname:"$host >>/tmp/deltaKerberos.log
        gen_keytab $user $host
    fi
fi
exit 0