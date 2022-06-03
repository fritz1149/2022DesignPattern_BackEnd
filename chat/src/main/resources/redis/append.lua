redis.call("rpush", KEYS[1], KEYS[2])
local state = redis.call("get", KEYS[3])
local ret = {"false"}
if state == "consistent"
    then
    ret[1] = "true"
    table.insert(ret, tostring(tonumber(redis.call("get", KEYS[4])) + tonumber(redis.call("llen", KEYS[1]))))
end

return ret