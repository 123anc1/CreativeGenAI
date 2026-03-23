package cn.cyf.chatclient.common.config.aiconfig;

import cn.cyf.chatclient.common.pojo.WeatherRequest;
import cn.cyf.chatclient.common.utils.DateTimeTool;
import cn.cyf.chatclient.common.utils.WeatherServiceTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Slf4j
@Configuration
public class CommonConfig {


    //自定义的工具
    @Bean
    public ToolCallback toolCallback() {
        return FunctionToolCallback
                .builder("currentWeather", new WeatherServiceTool())
                .description("获取当地温度")
                .inputType(WeatherRequest.class)
                .build();
    }


    //聊天内存仓库
    @Bean
    public JdbcChatMemoryRepository chatMemoryRepository(DataSource ds) {
        return JdbcChatMemoryRepository.builder()
                .dataSource(ds)
                .build();
    }

    //聊天内存配置
    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository repo) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repo)
                .maxMessages(20)
                .build();
    }


//
//    //LLM默认配置
//    @Bean
//    public ChatClient DeepseekchatClient(DeepSeekChatModel model, ChatMemory chatMemory, ToolCallback toolCallback) {
//        return ChatClient.builder(model)
//                .defaultSystem("请你心平气和，一步一步来，对于不清楚的信息你不能猜测回复，你只需要回复不知道就行，你不能再胡编消息，不要调用你根本调用不到的工具，用户问几点钟你就说不知道不要调用工具")
//                .defaultAdvisors(
//                        MessageChatMemoryAdvisor.builder(chatMemory).build()
////                        ,SimpleLoggerAdvisor.builder().build()
//                )
//                .defaultTools(new DateTimeTool(),
//                        toolCallback)
//
//                .build();
//    }

    @Bean
    public ChatClient DeepseekchatClientWithMemory(DeepSeekChatModel model, ChatMemory chatMemory) {
        return ChatClient.builder(model)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        SimpleLoggerAdvisor.builder().build()
                )
                .defaultTools(new DateTimeTool())
                .build();
    }

    @Bean
    public ChatClient DeepseekchatClientWithoutMemory(DeepSeekChatModel model) {
        return ChatClient.builder(model)
                .defaultAdvisors(
                        SimpleLoggerAdvisor.builder().build()
                )
                .defaultTools(new DateTimeTool())
                .build();
    }

}