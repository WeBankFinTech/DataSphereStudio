/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.entity;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appjoint.execution.common.NodeExecutionState;
import com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.execution.EventCheckerNodeExecutionAction;
import com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.service.AbstractEventCheck;
import com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.service.EventCheckerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author georgeqiao
 * @Title: EventChecker
 * @ProjectName Azkaban-EventChecker
 * @date 2019/9/1822:10
 * @Description: TODO
 */
public class EventChecker implements Runnable{
	public final static String WAIT_TIME = "max.receive.hours";
	public final static String WAIT_FOR_TIME = "wait.for.time";
	public final static String QUERY_FREQUENCY = "query.frequency";
	public final static String MSGTYPE="msg.type";
	public final static String SENDER="msg.sender";
	public final static String RECEIVER="msg.receiver";
	public final static String TOPIC="msg.topic";
	public final static String MSGNAME="msg.name";
	public final static String MSG="msg.body";
	public final static String EXEC_ID = "azkaban.flow.execid";
	public final static String SAVE_KEY="msg.savekey";
	public final static String USER_TIME="msg.init.querytime";
	public final static String TODAY="only.receive.today";
	public final static String AFTERSEND="msg.after.send";

	private Properties p;
	private String jobId;
	private int execId;
	private EventCheckerService wbDao=null;
	EventCheckerNodeExecutionAction backAction = null;
	public Long maxWaitTime;
	public int queryFrequency;

	private static Pattern pattern = Pattern.compile("[a-zA-Z_0-9@\\-]+");

	private static final Logger logger = Logger.getRootLogger();

	public EventChecker(String jobName, Properties p, EventCheckerNodeExecutionAction action) {
		this.p = p;
//		this.jobId = p.getProperty(EXEC_ID);
		this.jobId = "1";
		backAction = action;
		String waitTime = p.getProperty(EventChecker.WAIT_TIME, "1");
		Double doubleWaitTime = Double.valueOf(waitTime) * 3600 * 1000;
		maxWaitTime = Long.valueOf(doubleWaitTime.longValue());
		String query_frequency = p.getProperty(EventChecker.QUERY_FREQUENCY, "30000");
		queryFrequency = Integer.valueOf(query_frequency);
	}

	public void run() {
		try {
			backAction.setState(NodeExecutionState.Running);
//			getPid();
			if (p == null) {
				throw new RuntimeException("Properties is null. Can't continue");
			}
			if (checkParamMap(p, MSGTYPE)) {
				throw new RuntimeException("parameter " + MSGTYPE + " can not be blank.");
			}
			if (checkParamMap(p, TOPIC)) {
				throw new RuntimeException("parameter " + TOPIC + " can not be blank.");
			} else {
				String topic = p.getProperty(TOPIC);
				if (!topic.matches("[^_]*_[^_]*_[^_]*")) {
					throw new RuntimeException("Error format of topic parameter. Accept: XX_XX_XX.");
				}
			}
			if (checkParamMap(p, MSGNAME)) {
				throw new RuntimeException("parameter " + MSGNAME + " can not be blank.");
			}
			wbDao = EventCheckerService.getInstance();
			execId = Integer.parseInt(jobId);
			boolean success = false;
			if (p.getProperty(MSGTYPE).equals("SEND")) {
				if (checkParamMap(p, SENDER)) {
					throw new RuntimeException("parameter " + SENDER + " can not be blank.");
				} else {
					String sender = p.getProperty(SENDER);
					if (!sender.matches("[^@]*@[^@]*@[^@]*")) {
						throw new RuntimeException("Error format of  sender parameter. Accept: XX@XX@XX.");
					}
				}
//			if (checkParamMap(p, MSG)) {
//				throw new RuntimeException("Must specify a " + MSG
//				          + " key and value.");
//			}
				if (p.containsKey(MSG) && StringUtils.isNotEmpty(p.getProperty(MSG)) && p.getProperty(MSG).length() > 250) {
					throw new RuntimeException("parameter " + MSG + " length less than 250 !");
				}
				success = wbDao.sendMsg(execId, p, logger);
				if (success) {
					backAction.setState(NodeExecutionState.Success);

				} else {
					throw new RuntimeException("Failed Send message.");
				}
			}else if(p.getProperty(MSGTYPE).equals("RECEIVE")) {
				backAction.eventType("RECEIVE");
				receiveMsg();
			} else
			{
				throw new RuntimeException("Please input correct parameter of msg.type, Select RECEIVE Or SEND.");
			}
		}catch (Exception ex){
			backAction.setState(NodeExecutionState.Failed);
			throw ex;
		}

	}

	public  boolean   receiveMsg(){
		boolean success = false;
		if(p.getProperty(MSGTYPE).equals("RECEIVE")) {
			if (checkParamMap(p, RECEIVER)) {
				backAction.setState(NodeExecutionState.Failed);
				throw new RuntimeException("parameter " + RECEIVER + " can not be blank.");
			} else {
				String receiver = p.getProperty(RECEIVER);
				if (!receiver.matches("[^@]*@[^@]*@[^@]*")) {
					backAction.setState(NodeExecutionState.Failed);
					throw new RuntimeException("Error format of receiver parameter. Accept: XX@XX@XX.");
				}
			}
//			checkTimeParam(p, WAIT_FOR_TIME);
			String userTime = checkTimeParamMap(p, USER_TIME);
			if (StringUtils.isNotEmpty(userTime)) {
				p.put(USER_TIME, userTime);
			}
			success = wbDao.reciveMsg(execId, p, logger);
			if (success) {
				backAction.saveKeyAndValue(getJobSaveKeyAndValue());
				backAction.setState(NodeExecutionState.Success);
			} else {
				backAction.setState(NodeExecutionState.Running);
			}
		}
		return success;
	}

	public String  getJobSaveKeyAndValue(){
		Map<String,String> saveValueMap = new HashMap<>();
		String msgBody=p.getProperty(MSG, "{}");
		String saveKey=p.getProperty(SAVE_KEY,"msg.body");
		saveValueMap.put(saveKey, msgBody);
		Gson gson = new Gson();
		String saveValueJson = gson.toJson(saveValueMap);
	    logger.info("Output msg body: "+saveValueJson);
	    return saveValueJson;
	}

	public void cancel() throws InterruptedException {
//		AbstractEventCheck.closeDruidDataSource();
//		throw new RuntimeException("Kill this eventchecker.");
	}

	private boolean checkParamMap(Properties p, String key){
	    boolean checkFlag = false;
	    if(!p.containsKey(key)){
	        throw new RuntimeException("parameter " + key + " is Empty.");
	    }
	    if(p.containsKey(key)){
		    if(StringUtils.isEmpty(p.getProperty(key))){
	        checkFlag = true;
	      }
	    }
	    if(!MSG.equals(key) && StringUtils.contains(p.getProperty(key), " ")){
			throw new RuntimeException("parameter " + key + " can not contains space !");
		}
	    if(!checkNoStandardStr(p.getProperty(key))){
	    	throw new RuntimeException("parameter " + key + " Accept letter and number and _@- only.");
	    }
	    if(p.getProperty(key).length() > 45){
	    	throw new RuntimeException("parameter " + key + " length less than 45 !");
	    }
	    return checkFlag;
    }

	private boolean checkNoStandardStr(String param){
        Matcher matcher = pattern.matcher(param);
        return matcher.matches();
	}

	private void checkTimeParam(Properties p, String key){
		if(p.containsKey(key)){
			String waitForTime= p.getProperty(key);
			if(!waitForTime.matches("^(0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9])$")){
				throw new RuntimeException("Parameter " + key + " Time format error ! For example: HH:mm");
			}
		}
	}

	private String checkTimeParamMap(Properties p, String key){
	    if(p.containsKey(key)){
	    	String userTime = p.getProperty(key);
	    	Pattern ptime = Pattern.compile("^([1][7-9][0-9][0-9]|[2][0][0-9][0-9])(\\-)([0][1-9]|[1][0-2])(\\-)([0-2][1-9]|[3][0-1])(\\s)([0-1][0-9]|[2][0-3])(:)([0-5][0-9])(:)([0-5][0-9])$");
	        Matcher m = ptime.matcher(userTime);
	        if(!m.matches()){
	        	throw new RuntimeException("Parameter " + key + " Time format error ! For example: yyyy-MM-dd HH:mm:ss");
	        }
		    return userTime;
	    }else{
	    	return null;
	    }
    }

//	private String getPid(){
//		// get name representing the running Java virtual machine.
//		String name = ManagementFactory.getRuntimeMXBean().getName();
//		System.out.println(name);
//		// get pid
//		String pid = name.split("@")[0];
//		logger.info("EventCheck Pid is:" + pid);
//		return pid;
//	}

	public static void main(String[] args) {
		Properties p = new Properties();
		p.put("azkaban.flow.execid","111");

		p.put("msg.type","RECEIVE");
		p.put("msg.receiver","project@job@v_zhu");
		p.put("msg.topic","bdp_new_test");
		p.put("msg.name","TestCheck");
		p.put("msg.savekey","msg.body");
		p.put("query.frequency","60");
		p.put("wait.time","1");
		p.put("only.receive.today","true");
		p.put("msg.after.send","true");
//		p.put("wait.for.time","22:00");

//		p.put("msg.type","SEND");
//		p.put("msg.sender","project@job@v_zhu");
//		p.put("msg.topic","bdp_new_test");
//		p.put("msg.name","TestCheck");
//		p.put("msg.body","msg.body");

		p.put("msg.eventchecker.jdo.option.name","msg");
		p.put("msg.eventchecker.jdo.option.url","jdbc:mysql://127.0..0.1:3306/wtss_qyh_test?useUnicode=true&characterEncoding=UTF-8");
		p.put("msg.eventchecker.jdo.option.username","root");
		p.put("msg.eventchecker.jdo.option.password","MTIzNDU2");

//		EventChecker ec = new EventChecker("AA",p);
//		ec.run();
	}


}
