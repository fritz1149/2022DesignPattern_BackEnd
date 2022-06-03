local serverLatestId = tonumber(redis.call("get", KEYS[1]))
local clientLatestId = tonumber(KEYS[3])
if clientLatestId >= serverLatestId
    then
    local len = tonumber(redis.call("llen", KEYS[2]))
    redis.call("ltrim", KEYS[2], clientLatestId - serverLatestId, -1)
    redis.call("set", KEYS[1], math.min(clientLatestId, serverLatestId + len))
end
return {"OK"}
