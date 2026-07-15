
package com.ai.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Choice {

    private Integer index = 0;
    private ChatMessage message;

    @com.google.gson.annotations.SerializedName("finish_reason")
    private String finishReason = "";
}
