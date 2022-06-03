redis.call("rpush", KEYS[1], KEYS[2])
return redis.call("llen", KEYS[1])