package com.acme.newsletter.service;

import com.acme.newsletter.model.Content;
import com.acme.newsletter.model.dto.ContentCreationRequest;

public interface ContentService {
    Content createContent(ContentCreationRequest request);
}