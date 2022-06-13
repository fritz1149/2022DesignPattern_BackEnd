local len = tonumber(redis.call("llen", KEYS[1]))
local serverLatestId = tonumber(redis.call("get", KEYS[3]))
local clientLatestId = tonumber(KEYS[4])
local ret = {"false"}
if clientLatestId == serverLatestId and len == 0
then
    redis.call("set", KEYS[2], "consistent")
elseif clientLatestId == serverLatestId then
    redis.call("set", KEYS[2], "pushing")
    local messages = redis.call("lrange", KEYS[1], 0, 10)
    ret[1] = "true"
    table.insert(ret, tostring(serverLatestId))
    table.insert(ret, messages)
elseif clientLatestId < serverLatestId then
    redis.call("set", KEYS[2], "pushing")
    ret[1] = "lost"
end

return ret