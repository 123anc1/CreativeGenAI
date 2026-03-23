package cn.cyf.chatclient.common.utils;

import cn.cyf.chatclient.modules.community.mapper.SensitiveWordMapper;
import cn.cyf.chatclient.modules.community.model.SensitiveWord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 敏感词过滤器（基于Trie树实现）
 */
@Slf4j
@Component
public class SensitiveWordFilter {
    
    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;
    
    // Trie树节点
    private static class TrieNode {
        // 子节点，使用HashMap存储，支持任意字符
        private final Map<Character, TrieNode> children;
        // 是否是敏感词结尾
        private boolean isEnd;
        // 敏感等级
        private int level;
        
        public TrieNode() {
            this.children = new HashMap<>();
            this.isEnd = false;
            this.level = 0;
        }
    }
    
    // 根节点
    private TrieNode root;
    
    /**
     * 初始化敏感词库
     */
    @PostConstruct
    public void init() {
        log.info("开始初始化敏感词库...");
        root = new TrieNode();
        
        // 从数据库加载敏感词
        List<SensitiveWord> sensitiveWords = sensitiveWordMapper.getAllEnabledSensitiveWords();
        
        if (!CollectionUtils.isEmpty(sensitiveWords)) {
            for (SensitiveWord word : sensitiveWords) {
                addWord(word.getWord(), word.getLevel());
            }
            log.info("敏感词库初始化完成，加载了{}个敏感词", sensitiveWords.size());
        } else {
            log.warn("敏感词库为空");
        }
    }
    
    /**
     * 向Trie树中添加敏感词
     */
    private void addWord(String word, int level) {
        if (word == null || word.isEmpty()) {
            return;
        }
        
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (!node.children.containsKey(c)) {
                node.children.put(c, new TrieNode());
            }
            node = node.children.get(c);
        }
        node.isEnd = true;
        node.level = level;
    }
    
    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder(text);
        int length = text.length();
        
        for (int i = 0; i < length; i++) {
            int matchLength = checkSensitiveWord(text, i);
            if (matchLength > 0) {
                // 替换为*
                for (int j = 0; j < matchLength; j++) {
                    result.setCharAt(i + j, '*');
                }
                i += matchLength - 1;
            }
        }
        
        return result.toString();
    }
    
    /**
     * 检查是否包含敏感词
     * @param text 待检查文本
     * @param start 开始位置
     * @return 敏感词长度，0表示未匹配
     */
    private int checkSensitiveWord(String text, int start) {
        TrieNode node = root;
        int maxLength = 0;
        int currentLength = 0;
        
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            
            if (!node.children.containsKey(c)) {
                break;
            }
            
            node = node.children.get(c);
            currentLength++;
            
            if (node.isEnd) {
                maxLength = currentLength;
            }
        }
        
        return maxLength;
    }
    
    /**
     * 重新加载敏感词库
     */
    public void reload() {
        init();
    }
}