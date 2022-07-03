package org.emailclient;

public record EmailAttachment(String fileName, byte[] content, String mimeType) {


}
