package org.emailclient;

public class EmailAttachment {


    private final String fileName;
    private final byte[] content;

    private final String mimeType;


    public EmailAttachment(String fileName, byte[] content, String mimeType) {
        this.fileName = fileName;
        this.content = content;
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public String getMimeType() {
        return mimeType;
    }

}
