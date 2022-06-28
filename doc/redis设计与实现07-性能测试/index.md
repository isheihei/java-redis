---
title: "Redis设计与实现07 性能测试"
description: 
date: 2022-06-28T22:34:42+08:00
categories: ["数据库"]
tags: ["redis","java-redis"]
image: log.jpg
hidden: false
draft: true
weight: 107
---

## 本地测试结果

连接并发数为 50 两组，执行命令 set，lpush；请求数均为1000000

服务器内存均为 4G

### Redis

吞吐量：

- set ：`114626.32 requests per second`
- lpush：`114259.60 requests per second`

延迟：99% 以上小于 1ms

```shell
# redis-benchmark -h 127.0.0.1 -p 6379 -c 50 -t set,lpush -n 1000000
====== SET ======
  1000000 requests completed in 8.72 seconds
  50 parallel clients
  3 bytes payload
  keep alive: 1

99.99% <= 1 milliseconds
99.99% <= 7 milliseconds
99.99% <= 8 milliseconds
100.00% <= 9 milliseconds
100.00% <= 9 milliseconds
114626.32 requests per second

====== LPUSH ======
  1000000 requests completed in 8.75 seconds
  50 parallel clients
  3 bytes payload
  keep alive: 1

99.94% <= 1 milliseconds
99.96% <= 2 milliseconds
99.97% <= 3 milliseconds
99.97% <= 5 milliseconds
99.98% <= 6 milliseconds
99.98% <= 7 milliseconds
99.98% <= 8 milliseconds
99.99% <= 10 milliseconds
100.00% <= 12 milliseconds
100.00% <= 13 milliseconds
100.00% <= 13 milliseconds
114259.60 requests per second
```

### Java-edis

吞吐量：

- set ：`61747.45 requests per second`
- lpush：`60219.20 requests per second`

延迟：99% 以上 小于 1ms，但是极少数的最大延迟较高。


```shell
# redis-benchmark -h 127.0.0.1 -p 6379 -c 50 -t set,lpush -n 1000000
====== SET ======
  1000000 requests completed in 16.19 seconds
  50 parallel clients
  3 bytes payload
  keep alive: 1

99.78% <= 1 milliseconds
99.99% <= 2 milliseconds
100.00% <= 3 milliseconds
100.00% <= 4 milliseconds
100.00% <= 5 milliseconds
100.00% <= 6 milliseconds
100.00% <= 7 milliseconds
100.00% <= 8 milliseconds
100.00% <= 9 milliseconds
100.00% <= 10 milliseconds
100.00% <= 11 milliseconds
100.00% <= 12 milliseconds
100.00% <= 12 milliseconds
61747.45 requests per second

====== LPUSH ======
  1000000 requests completed in 16.53 seconds
  50 parallel clients
  3 bytes payload
  keep alive: 1

99.91% <= 1 milliseconds
99.99% <= 2 milliseconds
99.99% <= 3 milliseconds
99.99% <= 4 milliseconds
99.99% <= 5 milliseconds
99.99% <= 6 milliseconds
99.99% <= 7 milliseconds
99.99% <= 8 milliseconds
99.99% <= 9 milliseconds
99.99% <= 10 milliseconds
99.99% <= 11 milliseconds
99.99% <= 12 milliseconds
99.99% <= 13 milliseconds
99.99% <= 174 milliseconds
99.99% <= 175 milliseconds
100.00% <= 328 milliseconds
100.00% <= 329 milliseconds
100.00% <= 329 milliseconds
60488.75 requests per secondD:\software\redis>redis-benchmark -h 127.0.0.1 -p 6379 -c 50 -t set,lpush -n 1000000
====== SET ======
  1000000 requests completed in 16.92 seconds
  50 parallel clients
  3 bytes payload
  keep alive: 1

99.59% <= 1 milliseconds
99.96% <= 2 milliseconds
99.98% <= 3 milliseconds
99.99% <= 4 milliseconds
99.99% <= 12 milliseconds
99.99% <= 13 milliseconds
100.00% <= 79 milliseconds
100.00% <= 80 milliseconds
100.00% <= 81 milliseconds
100.00% <= 81 milliseconds
59112.14 requests per second

====== LPUSH ======
  1000000 requests completed in 16.61 seconds
  50 parallel clients
  3 bytes payload
  keep alive: 1

99.72% <= 1 milliseconds
99.97% <= 2 milliseconds
99.98% <= 3 milliseconds
99.99% <= 4 milliseconds
99.99% <= 8 milliseconds
99.99% <= 9 milliseconds
99.99% <= 10 milliseconds
99.99% <= 37 milliseconds
99.99% <= 38 milliseconds
99.99% <= 39 milliseconds
99.99% <= 40 milliseconds
99.99% <= 41 milliseconds
99.99% <= 42 milliseconds
99.99% <= 43 milliseconds
99.99% <= 50 milliseconds
99.99% <= 51 milliseconds
99.99% <= 52 milliseconds
99.99% <= 54 milliseconds
99.99% <= 56 milliseconds
99.99% <= 57 milliseconds
99.99% <= 58 milliseconds
99.99% <= 59 milliseconds
99.99% <= 60 milliseconds
99.99% <= 61 milliseconds
99.99% <= 62 milliseconds
100.00% <= 161 milliseconds
100.00% <= 162 milliseconds
100.00% <= 162 milliseconds
60219.20 requests per second
```

## 网络测试结果

测试环境为 2核2G 带宽4M 的云服务器

连接并发数均为 50，执行命令 set，lpush；请求数均为10000

### Redis

吞吐量：

- set ：`1430.21 requests per second`
- lpush：`1464.56 requests per second`

延迟：99% 以上小于 42ms，最大延迟 75ms

 ```shell
 # redis-benchmark -h **** -p 6379 -c 50 -t set,lpush -n 10000
 ====== SET ======
   10000 requests completed in 6.99 seconds
   50 parallel clients
   3 bytes payload
   keep alive: 1
 
 0.01% <= 26 milliseconds
 1.28% <= 27 milliseconds
 2.39% <= 28 milliseconds
 3.80% <= 29 milliseconds
 10.18% <= 30 milliseconds
 17.13% <= 31 milliseconds
 23.13% <= 32 milliseconds
 36.36% <= 33 milliseconds
 48.34% <= 34 milliseconds
 56.85% <= 35 milliseconds
 64.02% <= 36 milliseconds
 74.87% <= 37 milliseconds
 81.85% <= 38 milliseconds
 88.89% <= 39 milliseconds
 94.32% <= 40 milliseconds
 97.60% <= 41 milliseconds
 99.11% <= 42 milliseconds
 99.36% <= 43 milliseconds
 99.39% <= 44 milliseconds
 99.43% <= 45 milliseconds
 99.47% <= 46 milliseconds
 99.50% <= 47 milliseconds
 99.53% <= 48 milliseconds
 99.56% <= 49 milliseconds
 99.62% <= 50 milliseconds
 99.65% <= 51 milliseconds
 99.66% <= 53 milliseconds
 99.68% <= 54 milliseconds
 99.69% <= 55 milliseconds
 99.71% <= 56 milliseconds
 99.72% <= 57 milliseconds
 99.73% <= 58 milliseconds
 99.74% <= 61 milliseconds
 99.75% <= 63 milliseconds
 99.77% <= 66 milliseconds
 99.81% <= 67 milliseconds
 99.82% <= 68 milliseconds
 99.85% <= 69 milliseconds
 99.87% <= 70 milliseconds
 99.88% <= 71 milliseconds
 99.89% <= 72 milliseconds
 99.90% <= 73 milliseconds
 99.97% <= 75 milliseconds
 100.00% <= 75 milliseconds
 1430.21 requests per second
 
 ====== LPUSH ======
   10000 requests completed in 6.83 seconds
   50 parallel clients
   3 bytes payload
   keep alive: 1
 
 0.01% <= 26 milliseconds
 1.28% <= 27 milliseconds
 5.65% <= 28 milliseconds
 9.15% <= 29 milliseconds
 16.49% <= 30 milliseconds
 26.45% <= 31 milliseconds
 35.77% <= 32 milliseconds
 44.40% <= 33 milliseconds
 54.63% <= 34 milliseconds
 62.85% <= 35 milliseconds
 69.65% <= 36 milliseconds
 79.79% <= 37 milliseconds
 88.30% <= 38 milliseconds
 91.25% <= 39 milliseconds
 91.95% <= 40 milliseconds
 94.17% <= 41 milliseconds
 98.33% <= 42 milliseconds
 99.89% <= 43 milliseconds
 99.99% <= 44 milliseconds
 100.00% <= 46 milliseconds
 1464.56 requests per second
 ```

### Java-Redis

吞吐量：

- set ：`1317.87 requests per second`
- lpush：`1400.36 requests per second`

延迟：99% 以上小于 60ms，但是最大延迟 超过300ms。

```shell
# redis-benchmark -h **** -p 6379 -c 50 -t set,lpush -n 10000
====== SET ======
  10000 requests completed in 7.59 seconds
  50 parallel clients
  3 bytes payload
  keep alive: 1

0.01% <= 27 milliseconds
0.08% <= 28 milliseconds
2.08% <= 29 milliseconds
8.21% <= 30 milliseconds
14.83% <= 31 milliseconds
18.47% <= 32 milliseconds
26.71% <= 33 milliseconds
41.34% <= 34 milliseconds
51.24% <= 35 milliseconds
58.09% <= 36 milliseconds
66.99% <= 37 milliseconds
74.41% <= 38 milliseconds
79.02% <= 39 milliseconds
81.20% <= 40 milliseconds
85.14% <= 41 milliseconds
87.18% <= 42 milliseconds
88.04% <= 43 milliseconds
88.88% <= 44 milliseconds
89.67% <= 45 milliseconds
90.52% <= 46 milliseconds
91.24% <= 47 milliseconds
92.07% <= 48 milliseconds
92.88% <= 49 milliseconds
93.43% <= 50 milliseconds
93.99% <= 51 milliseconds
94.44% <= 52 milliseconds
94.94% <= 53 milliseconds
95.13% <= 54 milliseconds
95.55% <= 55 milliseconds
96.04% <= 56 milliseconds
96.56% <= 57 milliseconds
96.82% <= 58 milliseconds
97.24% <= 59 milliseconds
97.50% <= 60 milliseconds
97.87% <= 61 milliseconds
98.47% <= 62 milliseconds
98.89% <= 63 milliseconds
99.11% <= 64 milliseconds
99.30% <= 65 milliseconds
99.44% <= 66 milliseconds
99.57% <= 67 milliseconds
99.64% <= 68 milliseconds
99.70% <= 69 milliseconds
99.74% <= 73 milliseconds
99.75% <= 75 milliseconds
99.77% <= 76 milliseconds
99.81% <= 78 milliseconds
99.82% <= 79 milliseconds
99.83% <= 268 milliseconds
99.84% <= 269 milliseconds
99.85% <= 270 milliseconds
99.88% <= 271 milliseconds
99.91% <= 272 milliseconds
99.94% <= 278 milliseconds
99.95% <= 284 milliseconds
99.96% <= 285 milliseconds
99.98% <= 289 milliseconds
99.99% <= 295 milliseconds
100.00% <= 305 milliseconds
1317.87 requests per second

====== LPUSH ======
  10000 requests completed in 7.14 seconds
  50 parallel clients
  3 bytes payload
  keep alive: 1

0.01% <= 26 milliseconds
0.88% <= 27 milliseconds
4.02% <= 28 milliseconds
7.05% <= 29 milliseconds
14.14% <= 30 milliseconds
21.63% <= 31 milliseconds
27.10% <= 32 milliseconds
34.69% <= 33 milliseconds
47.98% <= 34 milliseconds
58.29% <= 35 milliseconds
64.47% <= 36 milliseconds
71.64% <= 37 milliseconds
79.58% <= 38 milliseconds
84.25% <= 39 milliseconds
86.65% <= 40 milliseconds
88.44% <= 41 milliseconds
91.56% <= 42 milliseconds
94.03% <= 43 milliseconds
95.01% <= 44 milliseconds
95.50% <= 45 milliseconds
96.05% <= 46 milliseconds
96.35% <= 47 milliseconds
96.78% <= 48 milliseconds
97.03% <= 49 milliseconds
97.31% <= 50 milliseconds
97.50% <= 51 milliseconds
97.63% <= 52 milliseconds
97.85% <= 53 milliseconds
98.03% <= 54 milliseconds
98.27% <= 55 milliseconds
98.42% <= 56 milliseconds
98.59% <= 57 milliseconds
98.66% <= 58 milliseconds
98.84% <= 59 milliseconds
98.96% <= 60 milliseconds
99.10% <= 61 milliseconds
99.30% <= 62 milliseconds
99.49% <= 63 milliseconds
99.57% <= 64 milliseconds
99.67% <= 65 milliseconds
99.75% <= 66 milliseconds
99.83% <= 67 milliseconds
99.87% <= 68 milliseconds
99.89% <= 69 milliseconds
99.92% <= 70 milliseconds
99.94% <= 265 milliseconds
99.95% <= 269 milliseconds
99.97% <= 270 milliseconds
99.98% <= 272 milliseconds
99.99% <= 276 milliseconds
100.00% <= 276 milliseconds
1400.36 requests per second
```

## 结论

本地环境下，瓶颈主要是在于内存以及命令处理速度，本地测试结果可以得出，Java-Redis 的吞吐量约为 Redis 的 60%。

网络环境下，网络延迟成为最大的瓶颈，在50并发下，Redis 和 Java-Redis 的吞吐量基本没有差别，可以看出，此时内存和命令处理速度并不是主要的限制因素。

延迟方面，Redis 比较稳定，较少出现极大延迟情况。而 Java-Redis 的延迟差强人意，在网络环境下会有约 0.1% 延迟超过 100ms。
