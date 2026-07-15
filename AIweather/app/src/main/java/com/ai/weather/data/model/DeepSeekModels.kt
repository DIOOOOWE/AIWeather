package com.ai.weather.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DeepSeek AI API 请求/响应模型
 * 使用chat/completions接口生成自然语言天气描述
 */
@Serializable
data class DeepSeekRequest(
    val model: String = "deepseek-chat",
    val messages: List<ChatMessage>,
    @SerialName("max_tokens") val maxTokens: Int = 400,
    val temperature: Double = 0.7,
    val stream: Boolean = false
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class DeepSeekResponse(
    val id: String = "",
    val choices: List<Choice> = emptyList()
)

@Serializable
data class Choice(
    val index: Int = 0,
    val message: ChatMessage,
    @SerialName("finish_reason") val finishReason: String = ""
)
