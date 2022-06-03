local len = tonumber(redis.call("llen", KEYS[1]))
if len > 0
    then
    redis.call("set", KEYS[2], "pushing")
end