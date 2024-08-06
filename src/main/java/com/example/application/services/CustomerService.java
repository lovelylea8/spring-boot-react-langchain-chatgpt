package com.example.application.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface CustomerService
{
    @SystemMessage("")
    //TokenStream chat(@MemoryId String chatId, @UserMessage String message);
    TokenStream chat(@UserMessage String message);
}
