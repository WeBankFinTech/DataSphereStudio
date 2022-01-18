package com.webank.wedatasphere.warehouse.utils;

import com.google.common.base.Strings;
import com.webank.wedatasphere.warehouse.exception.DwException;

import javax.annotation.Nullable;

public class PreconditionUtil {

    private PreconditionUtil() {}

    public static void checkState(boolean condition, DwException e) throws DwException {
        if (!condition) {
            throw e;
        }
    }

    public static void checkState(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) throws DwException {
        if (!condition) {
            throw new DwException(DwException.BUSINESS_ERROR, format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkArgument(boolean condition, DwException e) throws DwException {
        if (!condition) {
            throw e;
        }
    }

    public static void checkArgument(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) throws DwException {
        if (!condition) {
            throw new DwException(DwException.BUSINESS_ERROR, format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkStringArgumentNotBlank(String argument, DwException e) throws DwException {
        if (Strings.isNullOrEmpty(argument)) {
            throw e;
        }
    }

    // 检查字符串参数，成功则返回 trim() 后的字符串，否则抛出异常
    public static String checkStringArgumentNotBlankTrim(String argument, DwException e) throws DwException {
        if (Strings.isNullOrEmpty(argument)) {
            throw e;
        }
        return argument.trim();
    }

    private static String format(@Nullable String template, @Nullable Object... args) throws DwException {
        int numArgs = args == null ? 0 : args.length;
        template = String.valueOf(template);
        StringBuilder builder = new StringBuilder(template.length() + 16 * numArgs);
        int templateStart = 0;

        int i;
        int placeholderStart;
        for(i = 0; i < numArgs; templateStart = placeholderStart + 2) {
            placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }

            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
        }

        builder.append(template.substring(templateStart));
        if (i < numArgs) {
            builder.append(" [");
            builder.append(args[i++]);

            while(i < numArgs) {
                builder.append(", ");
                builder.append(args[i++]);
            }

            builder.append(']');
        }

        return builder.toString();
    }

}
