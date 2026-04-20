package com.plantcloud.plant.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantcloud.config.DeepSeekProperties;
import com.plantcloud.plant.service.PlantAiExplanationService;
import com.plantcloud.plant.vo.AiExplanationVO;
import com.plantcloud.plant.vo.RiskAnalysisResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlantAiExplanationServiceImpl implements PlantAiExplanationService {

    private static final String DEFAULT_SUMMARY = "当前环境存在异常，请注意植物状态变化。";
    private static final String DEFAULT_ADVICE = "建议适当调整环境条件，如温度、湿度或光照。";
    private static final String DEFAULT_WARNING = "请及时关注植物健康状态。";
    private static final String SYSTEM_PROMPT = "你是一个面向普通用户的植物养护助手。";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DeepSeekProperties deepSeekProperties;

    @Override
    public AiExplanationVO generateExplanation(RiskAnalysisResultVO result) {
        if (!StringUtils.hasText(deepSeekProperties.getApiKey())) {
            return defaultExplanation();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(deepSeekProperties.getApiKey());

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(buildRequestBody(result), headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    buildRequestUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || !StringUtils.hasText(response.getBody())) {
                return defaultExplanation();
            }

            String content = extractContent(response.getBody());
            if (!StringUtils.hasText(content)) {
                return defaultExplanation();
            }

            AiExplanationVO explanation = objectMapper.readValue(cleanJsonContent(content), AiExplanationVO.class);
            if (!StringUtils.hasText(explanation.getSummary())
                    || !StringUtils.hasText(explanation.getAdvice())
                    || !StringUtils.hasText(explanation.getWarning())) {
                return defaultExplanation();
            }
            return explanation;
        } catch (Exception ex) {
            return defaultExplanation();
        }
    }

    private Map<String, Object> buildRequestBody(RiskAnalysisResultVO result) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", deepSeekProperties.getModel());
        payload.put("messages", List.of(
                buildMessage("system", SYSTEM_PROMPT),
                buildMessage("user", buildUserPrompt(result))
        ));
        return payload;
    }

    private Map<String, String> buildMessage(String role, String content) {
        Map<String, String> message = new LinkedHashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String buildUserPrompt(RiskAnalysisResultVO result) {
        String triggerReasons = result.getTriggerReasons() == null || result.getTriggerReasons().isEmpty()
                ? "- 无明显触发原因"
                : result.getTriggerReasons().stream()
                .map(reason -> "- " + reason)
                .reduce((left, right) -> left + "\n" + right)
                .orElse("- 无明显触发原因");

        return """
                请根据以下植物环境风险分析结果，生成面向普通用户的解释与建议。
                输出必须是 JSON，格式如下：
                {
                  "summary": "...",
                  "advice": "...",
                  "warning": "..."
                }
                不要输出解释说明。
                不要输出 markdown。
                只输出 JSON。
                summary：解释风险原因。
                advice：给出具体可执行建议。
                warning：输出一句风险提醒。
                面向普通用户，语言简洁自然。
                不要使用专业术语。
                不要编造数据。

                植物名称：%s
                风险等级：%s
                风险类型：%s
                当前温度：%s℃
                当前湿度：%s%%
                当前光照：%s lux
                最近1小时温度变化：%s℃
                最近1小时湿度变化：%s%%
                最近1小时光照变化：%s lux

                触发原因：
                %s
                """.formatted(
                safeText(result.getPlantName()),
                safeText(result.getRiskLevel()),
                formatRiskTypes(result.getRiskType()),
                safeNumber(result.getTemperature()),
                safeNumber(result.getHumidity()),
                safeNumber(result.getLight()),
                safeNumber(result.getTempDelta()),
                safeNumber(result.getHumidityDelta()),
                safeNumber(result.getLightDelta()),
                triggerReasons
        );
    }

    private String buildRequestUrl() {
        String baseUrl = deepSeekProperties.getBaseUrl();
        if (baseUrl.endsWith("/")) {
            return baseUrl + "v1/chat/completions";
        }
        return baseUrl + "/v1/chat/completions";
    }

    private String extractContent(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        if (contentNode.isMissingNode() || contentNode.isNull()) {
            return null;
        }
        return contentNode.asText();
    }

    private String cleanJsonContent(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7).trim();
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3).trim();
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3).trim();
        }
        return trimmed;
    }

    private String formatRiskTypes(List<String> riskTypes) {
        if (riskTypes == null || riskTypes.isEmpty()) {
            return "[]";
        }
        return riskTypes.toString();
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private String safeNumber(Object value) {
        return value == null ? "0" : String.valueOf(value);
    }

    private AiExplanationVO defaultExplanation() {
        AiExplanationVO explanation = new AiExplanationVO();
        explanation.setSummary(DEFAULT_SUMMARY);
        explanation.setAdvice(DEFAULT_ADVICE);
        explanation.setWarning(DEFAULT_WARNING);
        return explanation;
    }
}
