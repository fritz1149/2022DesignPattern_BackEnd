@echo off
setlocal enabledelayedexpansion
set name[0]=account
set name[1]=connection
set name[2]=gateway
for /l %%i in (0,1,2) do (
    docker build -t 19231149/!name[%%i]! ./!name[%%i]!
    docker tag 19231149/!name[%%i]! ccr.ccs.tencentyun.com/2022im/!name[%%i]!
    docker push ccr.ccs.tencentyun.com/2022im/!name[%%i]!
)