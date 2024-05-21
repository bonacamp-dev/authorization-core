package com.bonacamp.authorization.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ObjectUtils;

import java.util.UUID;

/**
 * 문자열 변환, 편집 관련 기능 클래스
 */
public final class StringUtils {
    /**
     * 객체 인스터스 생성 제한
     */
    private StringUtils() {}

    /**
     * 데이터를 toString 변환하는 함수
     * Null 체크 후 toString으로 리턴
     * usage : StringUtils.toString("stringvalue");
     *
     * @param value Object 데이터
     * @return String 데이터 리턴
     */
    public static String toString(Object value) {

        try {

            if (CustomUtils.isNullOrEmpty(value)) {

                return "";
            }

            return ObjectUtils.nullSafeToString(value).trim();
        } catch (Exception e) {
            // 에러 발생 시 빈 값 리턴
            return "";
        }
    }

    /**
     * 문자를 왼쪽 기준으로 자르는 함수
     *
     * @param originValue 원본 문자
     * @param length      자르는 길이
     * @return len만큼의 길이 문자
     */
    public static String left(String originValue, int length) {

        try {

            if (originValue.length() < length) {

                length = originValue.length();
            }

            return originValue.substring(0, length);
        } catch (Exception e) {
            // 에러 발생 시 원본 값 리턴
            return originValue;
        }
    }

    /**
     * 문자를 오른쪽 기준으로 자르는 함수
     *
     * @param originValue 원본 문자
     * @param length      자르는 길이
     * @return len만큼의 길이 문자
     */
    public static String right(String originValue, int length) {

        try {

            if (originValue.length() < length) {

                length = originValue.length();
            }

            return originValue.substring(originValue.length() - length, originValue.length());
        } catch (Exception e) {
            // 에러 발생 시 원본 값 리턴
            return originValue;
        }
    }

    /**
     * 문자열을 지정한 자리만큼 자르는 함수
     *
     * @param originValue 기준 문자열
     * @param beginIndex  문자열의 시작 인덱스
     * @param endIndex    문자열의 종료 인덱스
     * @return 반환 문자열
     */
    public static String subString(String originValue, int beginIndex, int endIndex) {

        try {

            if (beginIndex < originValue.length() || endIndex < originValue.length()) {

                originValue = originValue.substring(beginIndex, endIndex);
            }

            return originValue;
        } catch (Exception e) {

            return originValue;
        }
    }

    /**
     * subString 기능에 기본값 추가된 함수
     *
     * @param originValue  원본 문자열
     * @param beginIndex   문자열의 시작 인덱스
     * @param endIndex     문자열의 종료 인덱스
     * @param defaultValue 기본 값
     * @return 반환 문자
     */
    public static String subString(String originValue, int beginIndex, int endIndex, String defaultValue) {

        if (CustomUtils.isNullOrEmpty(defaultValue)) {

            defaultValue = "";
        }

        if (CustomUtils.isNullOrEmpty(originValue)) {

            return defaultValue;
        }

        if (originValue.length() <= beginIndex || originValue.length() < endIndex) {

            return defaultValue;
        }

        try {

            return originValue.substring(beginIndex, endIndex);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * char 문자를 길이만큼 문자열로 변환하는 함수
     *
     * @param padChar char 타입 문자
     * @param length  문자열 길이
     * @return 반환하는 문자열
     */
    public static String toPadString(char padChar, int length) {

        if (length <= 0) {

            return "";
        }

        StringBuilder sb = new StringBuilder();

        while (sb.length() < length) {

            sb.append(padChar);
        }

        return sb.toString();

    }

    /**
     * 문자 가운데 정렬 함수
     *
     * @param value  정렬 대상 문자열
     * @param length 전체 문자열 사이즈
     * @return 가운데 정렬된 문자열 반환
     */
    public static String centerAlign(String value, int length) {

        try {
            //문자 길이 + 양쪽 공백 1씩(2) 보다 작은 사이즈이면 진행하지 않음.
            if (length < value.length() + 2) {

                return value;
            }

            //좌우 공간을 구한다.
            int spaceCount = (length - value.length()) / 2;

            if (spaceCount < 0) {

                spaceCount = 0;
            }

            //문자열 앞뒤에 값을 붙인다
            return StringUtils.toPadString(' ', spaceCount) + value + StringUtils.toPadString(' ', spaceCount);

        } catch (Exception e) {

            return value;
        }
    }

    /**
     * 문자 왼쪽 정렬 함수
     *
     * @param value  정렬 대상 문자열
     * @param length 전체 문자열 사이즈
     * @return 왼쪽 정렬된 문자열 반환
     */
    public static String leftAlign(String value, int length) {

        try {

            if (length < 0) {

                length = 0;
            }

            //공간 값을 만든다
            int spaceCount = length - value.length();

            if (spaceCount < 0) {

                spaceCount = 0;
            }

            return value + StringUtils.toPadString(' ', spaceCount);

        } catch (Exception e) {

            return value;
        }
    }

    /**
     * 문자 왼쪽 정렬 함수
     *
     * @param value  정렬 대상 문자열
     * @param length 전체 문자열 사이즈
     * @return 왼쪽 정렬된 문자열 반환
     */
    public static String rightAlign(String value, int length) {

        try {

            if (length < 0) {

                length = 0;
            }

            //공간 값을 만든다
            int spacelength = length - value.length();

            if (spacelength < 0) {

                spacelength = 0;
            }

            return StringUtils.toPadString(' ', spacelength) + value;

        } catch (Exception e) {

            return value;
        }
    }

    /**
     * 왼쪽 문자 채우는 함수
     *
     * @param value   원본 문자열
     * @param length  문자를 채운 전체 길이
     * @param padChar 채우는 문자
     * @return 왼쪽에 문자가 채워진 문자열 반환
     */
    public static String lpad(String value, int length, char padChar) {

        return StringUtils.toPadString(padChar, length) + value;
    }

    /**
     * 오른쪽 문자 채우는 함수
     *
     * @param value   원본 문자열
     * @param length  문자를 채운 전체 길이
     * @param padChar 채우는 문자
     * @return 오른쪽에 문자가 채워진 문자열 반환
     */
    public static String rpad(String value, int length, char padChar) {

        return value + StringUtils.toPadString(padChar, length);
    }

    /**
     * 금액 콤마 표시 문자 변환 함수
     *
     * @param value 금액
     * @return 금액 표시 문자열 반환
     */
    public static String toAmountString(Object value) {

        try {
            //Format처리
            String strFormat = "%,f";

            double doValue = Double.parseDouble(StringUtils.toString(value));
            //Format을 씌워서 리턴
            return String.format(strFormat, doValue);
        } catch (Exception e) {

            return StringUtils.toString(value);
        }
    }

    /**
     * 소수점 있는 금액 표시 변환 함수
     *
     * @param value 금액
     * @param point 소수점 자릿수
     * @return 소수점 반올림한 금액 문자 반환
     */
    public static String toRoundAmountString(Object value, int point) {

        try {
            // POINT값이 0보다 작으면 빈값 리턴
            if (point < 0) {

                return "";
            }

            // 소수점 자릿수는 10자리까지 제한
            if (point > 10) {

                point = 10;
            }

            //넘어온 값을 double로 변경한다
            double amount = Double.parseDouble(value.toString());
            double pointValue = Math.pow(10, point);

            //Round 처리한다
            amount = Math.round(amount * pointValue) / pointValue;

            //Format처리
            String strFormat = "%,";

            //값이 Null, 0, "" 이 아니면 소수점을 처리
            if (point > 0) {

                strFormat += "." + StringUtils.toString(point);
            }

            strFormat += "f";

            //Format을 씌워서 리턴
            return String.format(strFormat, amount);
        } catch (Exception e) {

            return StringUtils.toString(value);
        }
    }

    /**
     * 문자 마스킹 처리하는 함수
     * 시작하는 부분만 지정하고 문자의 끝까지 마스킹 처리한다.
     *
     * @param originValue 대상 문자열
     * @param startIndex  마스킹 처리하는 시작 인덱스
     * @return 마스킹된 문자열 반환
     */
    public static String toMaskString(String originValue, int startIndex) {

        return toMaskString(originValue, startIndex, originValue.length() - 1, "*");
    }

    /**
     * 문자 마스킹 처리하는 함수
     * 마스킹 시작과 끝의 인덱스를 지정하여 부분 마스킹을 한다.
     *
     * @param originValue 대상 문자열
     * @param beginIndex  마스킹 시작 인덱스
     * @param endIndex    마스킹 종료 인덱스
     * @return 마스킹된 문자열 반환
     */
    public static String toMaskString(String originValue, int beginIndex, int endIndex) {

        return toMaskString(originValue, beginIndex, endIndex, "*");
    }

    /**
     * 문자 마스킹 처리하는 함수
     * 마스킹 구간을 설정하고 마스킹 문자 타입을 받아서 변환한다.
     *
     * @param originValue 대상 문자열
     * @param beginIndex  마스킹 시작 인덱스
     * @param endIndex    마스킹 종료 인덱스
     * @param masktype    마스킹 처리 문자
     * @return 마스킹된 문자열 반환
     */
    public static String toMaskString(String originValue, int beginIndex, int endIndex, String masktype) {

        try {

            if (CustomUtils.isNullOrEmpty(originValue)) {

                return originValue;
            }

            if (CustomUtils.isNullOrEmpty(masktype)) {

                masktype = "*";
            }

            //넘겨받은 문자의 길이
            int valueLength = originValue.length();

            //Index 설정이 잘못 되어 있으면 끝으로 변경
            if ((beginIndex < valueLength && valueLength < endIndex) || beginIndex > endIndex) {

                endIndex = valueLength - 1;
            }

            if (beginIndex > valueLength || endIndex > valueLength) {

                return originValue;
            }

            //마스크 씌울 문자
            String maskStr = "";

            for (int i = 0; i < (endIndex - beginIndex + 1); i++) {
                maskStr += masktype;
            }

            //앞자리 + 마스크 + 뒷자리
            return originValue.substring(0, beginIndex) + maskStr + originValue.substring(endIndex + 1, originValue.length());
        } catch (Exception e) {

            return StringUtils.toString(originValue.length());
        }
    }

    public static String byteToString(byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        for (byte bt : bytes) {

            sb.append(String.format("%02x", bt));
        }
        return StringUtils.toString(sb);
    }

    /**
     * 특정 위치의 문자(char)를 제거하는 함수
     *
     * @param value 대상 문자열
     * @param index 제거하는 문자의 인덱스(위치)
     * @return 문자가 제거된 문자열 반환
     */
    public static String removeAt(String value, int index) {

        try {

            return StringUtils.subString(value, 0, index) + StringUtils.subString(value, index + 1, value.length());
        } catch (Exception e) {

            return value;
        }
    }

    /**
     * 문자열 연결 함수
     * usage : StringUtils.concat(10,"ABC", 123,55);
     *
     * @param args Object 타입의 복수 파라미터
     * @return 연결된 문자열 반환
     */
    public static String concat(Object... args) {

        StringBuffer tempStr = new StringBuffer();

        for (Object obj : args) {
            tempStr.append(StringUtils.toString(obj));
        }

        return StringUtils.toString(tempStr);
    }

    /**
     * 문자열 연결 함수
     * usage : StringUtils.concat("ABC", "DEFG");
     *
     * @param args String 타입의 복수 파라미터
     * @return
     */
    public static String concat(String... args) {

        StringBuffer tempStr = new StringBuffer();

        for (String str : args) {

            tempStr.append(str);
        }

        return StringUtils.toString(tempStr);
    }

    /**
     * 문자열 연결 함수
     * usage : StringUtils.concatAppendChar("/", "v1","api");
     *
     * @param addChar 문자열 사이에 추가하는 문자
     * @param args    String 타입의 복수 파라미터
     * @return "/v1/api/"
     */
    public static String addCharConcat(char addChar, String... args) {

        StringBuffer tempStr = new StringBuffer();

        for (String str : args) {

            tempStr.append(str).append(StringUtils.toString(addChar));
        }

        return StringUtils.toString(tempStr);
    }

    /**
     * 문자열 연결 함수
     * usage : StringUtils.concatAppendChar("/", false, "v1","api");
     *
     * @param addChar 문자열 사이에 추가하는 문자
     * @param isEnd   마지막 문자에 추가 여부
     * @param args    String 타입의 복수 파라미터
     * @return "/v1/api"
     */
    public static String addCharConcat(char addChar, boolean isEnd, String... args) {

        StringBuffer tempStr = new StringBuffer();

        for (String str : args) {

            tempStr.append(str);

            if (!(str.equals(args[args.length - 1]) && !isEnd)) {

                tempStr.append(StringUtils.toString(addChar));
            }
        }

        return StringUtils.toString(tempStr);
    }
    
    /**
     * Json 데이터를 String으로 변환
     * @param obj
     * @return
     */
    public static String writeValueAsString(Object obj) {

        String payload = "";
        ObjectMapper mapper = new ObjectMapper();

        try {

            // System.out.println("[Request Data] payload : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
            //String test = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            payload = mapper.writeValueAsString(obj);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return payload;
    }

    /**
     * UUID 문자열을 리턴한다
     *
     * @return UUID 문자열
     */
    public static String getUUID() {
        return generateUUID().toString();
    }

    /**
     * UUID 객체를 리턴한다
     *
     * @return UUID
     */
    private static UUID generateUUID() {
        return UUID.randomUUID();
    }


    /**
     * value 숫자만 남기고 모든 문자열 제거
     * @param value
     * @return number only
     */
    public static String removeSpecialCharacter(String value) {

    	String regExp = "[^0-9]";
    	
    	return value.replaceAll(regExp, "");
    }
    
}