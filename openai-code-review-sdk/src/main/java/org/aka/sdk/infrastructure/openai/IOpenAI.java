package org.aka.sdk.infrastructure.openai;

import org.aka.sdk.infrastructure.openai.dto.ChatCompletionRequestDTO;

public interface IOpenAI {
    ChatCompletionRequestDTO completions(ChatCompletionRequestDTO requestDTO) throws Exception;
}
