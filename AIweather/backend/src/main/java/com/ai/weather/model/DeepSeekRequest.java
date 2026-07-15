
package com.ai.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeepSeekRequest {

    @Builder.Default
    private String model = "deepseek-chat";

    private List<ChatMessage> messages;

    @com.google.gson.annotations.SerializedName("max_tokens")
    @Builder.Default
    private Integer maxTokens = 400;

    @Builder.Default
    private Double temperature = 0.7;

    @Builder.Default
    private Boolean stream = false;
}
