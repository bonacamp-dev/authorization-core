package com.bonacamp.authorization.core.util;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * 메소드 재정의 함수 또는 기능 유틸 클래스
 */
public final class CustomUtils {

    /**
     * 객체 인스턴스 제한
     */
    private CustomUtils() {}
    
    /**
     * 데이터 null 또는 빈 값 체크
     *
     * @param value 체크할 데이터
     * @return boolean 타입 true / false
     */
    public static boolean isNullOrEmpty(Object value) {

        if (value == null) {

            return true;
        }

        if ((value instanceof String) && (((String) value).trim().length() == 0)) {

            return true;
        }

        if (value instanceof Map) {

            return ((Map<?, ?>) value).isEmpty();
        }

        if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }

        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }

        if (value instanceof Optional) {
            return !((Optional<?>) value).isPresent();
        }

        if (value instanceof CharSequence) {
            return ((CharSequence) value).length() == 0;
        }

        if (value instanceof List) {

            return ((List<?>) value).isEmpty();
        }

        if (value instanceof Object[]) {

            return (((Object[]) value).length == 0);
        }

        return false;
    }

    /**
     * 널값인 경우 기본값 설정하는 함수
     *
     * @param target       널 체크 값
     * @param defaultValue 변경할 기본값
     * @return 널이면 기본값, 아니면 대상 값 리턴
     */
    public static Object ifNullValue(Object target, Object defaultValue) {

        if (CustomUtils.isNullOrEmpty(target)) {

            return defaultValue;
        }

        return target;
    }

    /**
     * 데이터가 숫자인지 체크하는 함수
     *
     * @param value Object 타입의 숫자 검증할 데이터
     * @return 숫자이면 true, 아니면 false 반환
     */
    public static boolean isNumber(Object value) {

        boolean isNum = true;
        String strVal = StringUtils.toString(value);

        if (strVal == null || strVal.length() == 0) {

            isNum = false;
        } else {

            for (int i = 0; i < strVal.length(); i++) {

                int charVal = (int) strVal.charAt(i);

                // 숫자가 아니라면
                if (charVal < 48 || charVal > 57) {

                    isNum = false;
                }
            }
        }

        return isNum;
    }

    /**
     * 데이터 동등 비교 함수
     *
     * @param objA Object 데이터 A
     * @param objB Object 데이터 B
     * @return boolean 타입 true / false
     */
    public static boolean isEqual(Object objA, Object objB) {

        return Objects.equals(objA, objB);
    }

    /**
     * 데이터 동등 비교 함수
     *
     * @param objA Object 데이터 A
     * @param strB String 데이터 B
     * @return boolean 타입 true / false
     */
    public static boolean isEqual(Object objA, String strB) {

        return StringUtils.toString(objA).equals(strB);
    }

    /**
     * 데이터 동등 비교 함수
     *
     * @param objA Object 데이터 A
     * @param fltB float 데이터 B
     * @return boolean 타입 true / false
     */
    public static boolean isEqual(Object objA, float fltB) {

        return Float.parseFloat(StringUtils.toString(objA)) == fltB;
    }

    /**
     * 데이터 동등 비교 함수
     *
     * @param numA int 데이터 A
     * @param numB int 데이터 B
     * @return boolean 타입 true / false
     */
    public static boolean isEqual(int numA, int numB) {

        return numA == numB;
    }

    /**
     * Random으로 byte 데이터 리턴하는 함수
     * usage : salt - getRandByte(16);
     *
     * @param size byte 변환 사이즈
     * @return byte 변환된 문자열 값
     */
    public static String getRandomByteToString(int size) {

        SecureRandom random = new SecureRandom();

        byte[] temp = new byte[size];

        random.nextBytes(temp);

        return StringUtils.byteToString(temp);
    }

    /**
     * 랜덤 숫자를 리턴하는 함수
     *
     * @param length 숫자 길이
     * @return 0~9까지 숫자 중 파라미터 길이만큼 생성한 숫자를 문자로 리턴
     */
    public static String getRandomNumberToString(int length) {

        Random random = new Random();
        StringBuilder number = new StringBuilder(); //난수가 저장될 변수
        String singleNumber = "";

        for (int i = 0; i < length; i++) {

            //0~9 까지 난수 생성
            singleNumber = Integer.toString(random.nextInt(10));

            number.append(singleNumber);
        }

        return number.toString();
    }
}
