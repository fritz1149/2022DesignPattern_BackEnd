local serverLatestId = tonumber(redis.call("get", KEYS[1]))
redis.call("ltrim", KEYS[2], tonumber(KEYS[3]) - serverLatestId, -1)
redis.call("set", KEYS[1], KEYS[3])
return {"OK"}
