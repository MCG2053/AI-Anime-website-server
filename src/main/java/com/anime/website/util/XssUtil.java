package com.anime.website.util;

import java.util.regex.Pattern;

/**
 * XSS防护工具类
 * 用于过滤用户输入中的恶意脚本
 */
public class XssUtil {
    
    private static final Pattern[] PATTERNS = {
        // Script标签
        Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        // 事件处理器
        Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
        // JavaScript协议
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        // VBScript协议
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        // data协议
        Pattern.compile("data:text/html", Pattern.CASE_INSENSITIVE),
        // iframe标签
        Pattern.compile("<iframe[^>]*>.*?</iframe>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        // object标签
        Pattern.compile("<object[^>]*>.*?</object>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        // embed标签
        Pattern.compile("<embed[^>]*>", Pattern.CASE_INSENSITIVE),
        // expression
        Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE),
    };
    
    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("[<>\"'&]");
    
    /**
     * 过滤XSS攻击字符
     * @param value 需要过滤的字符串
     * @return 过滤后的安全字符串
     */
    public static String sanitize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        String result = value;
        
        // 移除危险模式
        for (Pattern pattern : PATTERNS) {
            result = pattern.matcher(result).replaceAll("");
        }
        
        // 转义HTML特殊字符
        result = escapeHtml(result);
        
        return result.trim();
    }
    
    /**
     * 转义HTML特殊字符
     * @param value 需要转义的字符串
     * @return 转义后的字符串
     */
    public static String escapeHtml(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        StringBuilder sb = new StringBuilder(value.length() * 2);
        for (char c : value.toCharArray()) {
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * 检查字符串是否包含潜在的XSS攻击
     * @param value 需要检查的字符串
     * @return 如果包含潜在XSS攻击返回true
     */
    public static boolean containsXss(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        
        for (Pattern pattern : PATTERNS) {
            if (pattern.matcher(value).find()) {
                return true;
            }
        }
        
        return false;
    }
}
