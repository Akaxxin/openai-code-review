curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiMzMxYjc5ZTVmMWVhZTYyOGU0NmQwYzhkNmZmYWU3MTkiLCJleHAiOjE3MjM0NzE5MzUyMDYsInRpbWVzdGFtcCI6MTcyMzQ3MDEzNTIyMn0.qpLKqAT8aVarOnMqb8Oc4o-xRiTFDMZGuzxVCgjaUtI" \
        -H "Content-Type:application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -d '{
          "model":"glm-4",
          "stream": "true",
          "messages": [
              {
                  "role": "user",
                  "content": "1+1"
              }
          ]
        }' \
  https://open.bigmodel.cn/api/paas/v4/chat/completions
