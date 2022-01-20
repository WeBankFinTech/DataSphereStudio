package com.webank.wedatasphere.dss.framework.admin.common.utils;

import com.webank.wedatasphere.dss.framework.admin.common.domain.PasswordResult;
import com.webank.wedatasphere.dss.framework.admin.common.domain.PositionBo;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class PasswordUtils {
    /**
     * 密码强度正则匹配
     */
    private static final String PWD_STRENGTH_REGEX
            = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*#_~?&^])[A-Za-z0-9@$!%*#_~?&^]{8,26}$";

    /**
     * 密码以字母开头
     */
    private static final String PWD_STARTER_REGEX = "^[a-zA-z].*";
    /**
     * 字符表行数
     */
    private static final int CHAR_TABLE_ROW_NUM = 4;
    /**
     * 字符表列数
     */
    private static final int CHAR_TABLE_COLUMN_NUM = 13;
    /**
     * 常规字符表
     */
    private static final char[][] charTable = {{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '=', '\0'},
            {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '[', ']', '\\'},
            {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', '\'', '\0', '\0'},
            {'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/', '\0', '\0', '\0'}};
    /**
     * shift下的字符表
     */
    private static final char[][] charTableWithShift = {
            {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '\0'},
            {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '{', '}', '|'},
            {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ':', '"', '\0', '\0'},
            {'z', 'x', 'c', 'v', 'b', 'n', 'm', '<', '>', '?', '\0', '\0', '\0'}};
    /**
     * 加密编码
     */
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private PasswordUtils() {
    }

    /**
     * 密码规则
     *
     * @return
     */
    public static PasswordResult checkPwd(String password, DssAdminUser user) {

        // 1.数字、大写字母、小写字母、特殊符号的组合，长度为8-26位
        if (!StringUtils.hasText(password) || !password.matches(PWD_STRENGTH_REGEX)) {
            return PasswordResult.PASSWORD_STRENGTH_ERROR;
        }
        // 2.排除与用户名相关
        else if (StringUtils.hasText(user.getUserName()) && password.contains(user.getUserName())) {
            return PasswordResult.PASSWORD_RELATED_USERNAME_ERROR;
        }
        // 3.密码须以字母开头
        else if (!StringUtils.hasText(password) || !password.matches(PWD_STARTER_REGEX)) {
            return PasswordResult.PASSWORD_STARTER_ERROR;
        }
        // 4.排除键盘序
        else if (isKeyBoardContinuous(password)) {
            return PasswordResult.PASSWORD_KEYBOARD_CONTINUOUS_ERROR;
        }


        return PasswordResult.PASSWORD_RULE_PASS;
    }

    /**
     * 键盘序检测
     */
    private static boolean isKeyBoardContinuous(String password) {
        List<PositionBo> positions = new ArrayList<>();

        for (int i = 0; i < password.length(); ++i) {
            PositionBo position = getPositionFromKeyBoard(password.toLowerCase().charAt(i));
            position = (position == null) ? new PositionBo(-1, -1) : position;
            positions.add(position);
        }

        for (int i = 1; i < password.length() - 1; ++i) {
            PositionBo front = positions.get(i - 1);
            PositionBo middle = positions.get(i);
            PositionBo end = positions.get(i + 1);
            // 正向连续（asd）或者反向连续（dsa）
            if (front.getRow() == middle.getRow() && middle.getRow() == end.getRow()) {
                if (front.getColumn() + 1 == middle.getColumn() && middle.getColumn() + 1 == end.getColumn()) {
                    return true;
                } else if (front.getColumn() - 1 == middle.getColumn() && middle.getColumn() - 1 == end.getColumn()) {
                    return true;
                }
            }
            // 正向连续（qaz）或者反向连续（zaq）
            if (front.getColumn() == middle.getColumn() && middle.getColumn() == end.getColumn()) {
                if (front.getRow() + 1 == middle.getRow() && middle.getRow() + 1 == end.getRow()) {
                    return true;
                } else if (front.getRow() - 1 == middle.getRow() && middle.getRow() - 1 == end.getRow()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static PositionBo getPositionFromKeyBoard(char ch) {
        for (int row = 0; row < CHAR_TABLE_ROW_NUM; ++row) {
            for (int column = 0; column < CHAR_TABLE_COLUMN_NUM; ++column) {
                if (ch == charTable[row][column] || ch == charTableWithShift[row][column]) {
                    return new PositionBo(row, column);
                }
            }
        }
        return null;
    }


}
