@echo off
setlocal enabledelayedexpansion
set name[0]=account
set name[1]=connection
set name[2]=gateway
set name[3]=chat
set name[4]=file
for /l %%i in (0,1,4) do (
    docker build -t 19231149/!name[%%i]! ./!name[%%i]!
    docker tag 19231149/!name[%%i]!:latest ccr.ccs.tencentyun.com/2022im/!name[%%i]!:latest
    docker push ccr.ccs.tencentyun.com/2022im/!name[%%i]!:latest
)