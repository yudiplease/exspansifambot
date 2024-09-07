package dev.yudiplease.exspansi.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.yudiplease.exspansi.bot.entity.WarnInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class WarnService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarnService.class);
    private static final String FILE_PATH = "warns.json";
    private Map<String, WarnInfo> warns;
    private ObjectMapper objectMapper;
    private final UserService userService;

    public WarnService(UserService userService) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        load();
    }

    
    public void load() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                warns = objectMapper.readValue(file, objectMapper.getTypeFactory()
                        .constructMapType(HashMap.class, String.class, WarnInfo.class));
            } else {
                warns = new HashMap<>();
            }
            LOGGER.info("Информация о варнах успешно загружена.");
        } catch (IOException e) {
            LOGGER.error(String.format("Не получилось загрузить варны из базы, причина: %s", e.getMessage()));
            warns = new HashMap<>();
        }
    }

    
    public void add(String id, WarnInfo warnInfo) {
        String staticId = warnInfo.getStaticId();
        if (!staticId.startsWith("#")) {
            staticId = "#" + warnInfo.getStaticId();
            warnInfo.setStaticId(staticId);
            warns.put(String.format("%s.%s", warnInfo.getStaticId(), UUID.randomUUID()), new WarnInfo(id, warnInfo.getStaticId(), warnInfo.getIssueDate(), warnInfo.getReason()));
        } else {
            warns.put(String.format("%s.%s", warnInfo.getStaticId(), UUID.randomUUID()), new WarnInfo(id, warnInfo.getStaticId(), warnInfo.getIssueDate(), warnInfo.getReason()));
        }
        save();
    }
    public WarnInfo getById(String staticId, String id) {
        String correctStaticId = staticId.startsWith("#") ? staticId : "#" + staticId;
        Iterator<Map.Entry<String, WarnInfo>> iterator = warns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WarnInfo> entry = iterator.next();
            if (entry.getKey().startsWith(correctStaticId) && entry.getValue().getWarnId().equals(id)) {
                return warns.get(entry.getKey());
            }
        }
        return null;
    }
    public void save() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), warns);
            LOGGER.info("Данные о варне успешно сохранены.");
        } catch (IOException e) {
            LOGGER.error(String.format("Не удалось сохранить варн в базу, причина: %s", e.getMessage()));
        }
    }

    
    public void deleteById(String staticId, String warnId) {
        String correctStaticId = staticId.startsWith("#") ? staticId : "#" + staticId;
        Iterator<Map.Entry<String, WarnInfo>> iterator = warns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WarnInfo> entry = iterator.next();
            if (entry.getKey().startsWith(correctStaticId) && entry.getValue().getWarnId().equals(warnId)) {
                iterator.remove();
                save();
                LOGGER.info("Варн с ID {} и staticId {} успешно удален.", warnId, correctStaticId);
                return;
            }
        }
        LOGGER.info("Варн с ID {} и staticId {} не найден.", warnId, correctStaticId);
    }
}
