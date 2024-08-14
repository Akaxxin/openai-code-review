package org.aka.sdk;


import org.aka.sdk.domain.service.impl.OpenAiCodeReviewService;
import org.aka.sdk.infrastructure.git.GitCommand;
import org.aka.sdk.infrastructure.openai.IOpenAI;
import org.aka.sdk.infrastructure.openai.impl.ChatGLM;
import org.aka.sdk.infrastructure.weixin.Weixin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





public class OpenAiCodeReview {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiCodeReview.class);


    //微信配置
    private String weixin_appid = "wx54e72a2bed34212d";
    private String weixin_secret = "35cfa3322729929ff907b63fd5d4ddcd";
    private String weixin_touser = "oiPhG6XYMUpWxygU5MCI00FAjZbo";
    private String weixin_template_id = "b2tmRyBSF5i9AYAGt6N7lnAUaY66Po5gg-OYjwWye4I";

    // ChatGLM 配置
    private String chatglm_apiHost = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private String chatglm_apiKeySecret = "";

    // Github 配置
    private String github_review_log_uri;
    private String github_token;

    // 工程配置 - 自动获取
    private String github_project;
    private String github_branch;
    private String github_author;

    public static void main(String[] args) throws Exception {
        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        /**
         * 项目：{{repo_name.DATA}} 分支：{{branch_name.DATA}} 作者：{{commit_author.DATA}} 说明：{{commit_message.DATA}}
         */
        Weixin weiXin = new Weixin(
                getEnv("WEIXIN_APPID"),
                getEnv("WEIXIN_SECRET"),
                getEnv("WEIXIN_TOUSER"),
                getEnv("WEIXIN_TEMPLATE_ID")
        );



        IOpenAI openAI = new ChatGLM(getEnv("CHATGLM_APIHOST"), getEnv("CHATGLM_APIKEYSECRET"));

        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(gitCommand, openAI, weiXin);
        openAiCodeReviewService.exec();

        logger.info("openai-code-review done!");
    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }


}




//        //1.代码检出
//        ProcessBuilder processBuilder = new ProcessBuilder("git","diff","HEAD~1","HEAD");
//        processBuilder.directory(new File("."));
//        Process process = processBuilder.start();
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String line;
//
//        StringBuilder diffCode = new StringBuilder();
//        while ((line = reader.readLine())!=null){
//            diffCode.append(line);
//        }
//        int exitCode = process.waitFor();
//        System.out.println("Exited with code:" + exitCode);
//        System.out.println("diff code" +diffCode.toString());
//
//        //2.chatglm 代理评审
//        String log = codeReview(diffCode.toString());
//        System.out.println("code review"+log);
//
//
//        //3.写入评审日志
//        String logURL = writeLong(token,log);
//        System.out.println("writeLog"+logURL);
//
//
//        //4.消息通知
//        System.out.println("pushMessage"+logURL);
//        pushMessage(logURL);


//    private static void pushMessage(String logUrl){
//        String accessToken = WXAccessTokenUtils.getAccessToken();
//        System.out.println(accessToken);
//        TemplateMessageDTO templateMessageDTO =new TemplateMessageDTO();
//        templateMessageDTO.put("project","big-market");
//        templateMessageDTO.put("review","feat:新加功能");
//        templateMessageDTO.setUrl(logUrl);
//        templateMessageDTO.setTemplate_id("-0K4zmFLE73_DOYQKAFiKYjkMOO-fIF7U-FALpi-CYU");
//
//        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken);
//        sendPostRequest(url,JSON.toJSONString(templateMessageDTO));
//    }
//    private static void sendPostRequest(String urlString, String jsonBody) {
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json; utf-8");
//            conn.setRequestProperty("Accept", "application/json");
//            conn.setDoOutput(true);
//
//            try (OutputStream os = conn.getOutputStream()) {
//                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
//                os.write(input, 0, input.length);
//            }
//
//            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
//                String response = scanner.useDelimiter("\\A").next();
//                System.out.println(response);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private static String codeReview(String diffCode) throws Exception {
//        String apiKeySecret = "331b79e5f1eae628e46d0c8d6ffae719.nNgl5YZ4SkIV6Ihp";
//        String token = BearerTokenUtils.getToken(apiKeySecret);
//
//        URL url = new URL("https://open.bigmodel.cn/api/paas/v4/chat/completions");
//        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Authorization","Bearer "+token);
//        connection.setRequestProperty("Content-Type","application/json");
//        connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//        connection.setDoOutput(true);
//
//
//
//        ChatCompletionRequestDTO chatCompletionRequestDTO = new ChatCompletionRequestDTO();
//        chatCompletionRequestDTO.setModel(Model.GLM_4_FLASH.getCode());
//        chatCompletionRequestDTO.setMessages(new ArrayList<ChatCompletionRequestDTO.Prompt>() {
//            private static final long serialVersionUID = -7988151926241837899L;
//
//            {
//                add(new ChatCompletionRequestDTO.Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:"));
//                add(new ChatCompletionRequestDTO.Prompt("user", diffCode));
//            }
//        });
//
//        try (OutputStream os = connection.getOutputStream()) {
//            byte[] input = JSON.toJSONString(chatCompletionRequestDTO).getBytes(StandardCharsets.UTF_8);
//            os.write(input);
//        }
//
//        int responseCode = connection.getResponseCode();
//        System.out.println(responseCode);
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String inputLine;
//
//        StringBuilder content = new StringBuilder();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
//
//        in.close();
//        connection.disconnect();
//
//        System.out.println("评审结果：" + content.toString());
//
//        ChatCompletionSyncResponseDTO response = JSON.parseObject(content.toString(), ChatCompletionSyncResponseDTO.class);
//        return response.getChoices().get(0).getMessage().getContent();
//
//
//    }
//
//    private static String writeLong(String token,String log) throws Exception {
//        Git git = Git.cloneRepository().setURI("https://github.com/Akaxxin/openai-code-reivew-log.git")
//                .setDirectory(new File("repo"))
//                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token,""))
//                .call();
//
//        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//        File dateFolder = new File("repo/"+dateFolderName);
//        if (!dateFolder.exists()){
//            dateFolder.mkdirs();
//        }
//        String fileName = generateRandomString(12)+".md";
//        File newFile = new File(dateFolder,fileName);
//        try(FileWriter writer = new FileWriter(newFile)){
//            writer.write(log);
//        }
//        git.add().addFilepattern(dateFolderName+"/"+fileName).call();
//        git.commit().setMessage("Add new file via GitHub Action").call();
//        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();
//
//        System.out.println("Changes have been pushed to the repository.");
//
//        return "https://github.com/Akaxxin/openai-code-reivew-log/blob/master/" + dateFolderName + "/" + fileName;
//
//
//    }
//
//    private static String generateRandomString(int length) {
//        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//        Random random = new Random();
//        StringBuilder sb = new StringBuilder(length);
//        for (int i = 0; i < length; i++) {
//            sb.append(characters.charAt(random.nextInt(characters.length())));
//        }
//        return sb.toString();
//    }




