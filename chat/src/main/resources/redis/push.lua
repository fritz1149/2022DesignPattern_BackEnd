local len = tonumber(redis.call("llen", KEYS[1]))
local ret = {"false"}
if len == 0
then
    redis.call("set", KEYS[2], "consistent")
else
    redis.call("set", KEYS[2], "pushing")
    local serverLatestId = redis.call("get", KEYS[3])
    local messages = redis.call("lrange", KEYS[1], 0, 10)
    ret[1] = "true"
    table.insert(ret, serverLatestId)
    table.insert(ret, messages)
end

return ret