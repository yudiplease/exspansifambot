package dev.yudiplease.exspansi.bot.util;

import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class AttachmentHandler {
//    public Mono<Void> handleAttachment(Attachment attachment, MessageChannel channel) {
//        return
//    }

//    private Mono<File> getFileFromAttachment(Attachment attachment) {
//        return Mono.fromCallable(() -> {
//            String fileName = attachment.getFilename();
//            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
//            Path tempFile = tempDir.resolve(fileName);
//
//            try {
//                Files.copy(attachment.getData()..openStream(), tempFile);
//                return tempFile.toFile();
//            } catch (IOException e) {
//                System.err.println("Error downloading attachment: " + e.getMessage());
//                return null;
//            }
//        });
//    }
}
