redis.call("rpush", KEYS[1], KEYS[2])
local state = redis.call("get", KEYS[3])
local ret = {"false"}
if state == "consistent"
    then
    ret[1] = "true"
    table.insert(ret, redis.call("get", KEYS[4]))
    redis.call("set", KEYS[3], "pushing")
end

return ret