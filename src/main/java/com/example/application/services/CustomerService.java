package com.example.application.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface CustomerService
{
    @SystemMessage("너는 회사 인사규정을 알려주는 챗봇이야.")
    //TokenStream chat(@MemoryId String chatId, @UserMessage String message);
    TokenStream chat(@UserMessage String message);
}
