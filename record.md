# observer处理过程
## leader election



# 2 master处理过程
## 2.1 任务
+ 2.1.1 初始化任务
+ 2.1.2 将任务添加到未处理列表
+ 2.1.3 将任务分配到不同的结点
+ 2.1.4 接收不同结点的任务完成情况
+ 2.1.5 任务重分配（与2.2.2相同）
    + 已发送的下线任务如何处理，使其不会再处理

## 2.2 成员管理
+ 2.2.1 对结点状态的管理
+ 2.2.2 如有机器下线对下线机器任务的重分配（与2.1.5相同）
    + 已发送的下线任务如何处理，使其不会再处理
+ 2.2.3 新加入机器的管理

# 3 slave处理过程
## 3.1 任务
+ 3.1.1 接收任务
+ 3.1.2 处理任务
+ 3.1.3 将任务完成信息发送至master（采用打包的方式，完成一整个job后发送，粒度较粗）