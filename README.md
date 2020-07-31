# shiro-urldns 检测

用于检测是否存在shiro反序列化漏洞

新增`PrincipalCollection`检测

## 构建

`mvn clean package -DskipTests`

## 效果

![](https://i.loli.net/2019/09/26/sJlDkEQUL2bjuP1.png)

![](https://i.loli.net/2020/07/31/v84FSBNpDyWHbxL.png)


## 致谢

https://github.com/frohoff/ysoserial

https://blog.paranoidsoftware.com/triggering-a-dns-lookup-using-java-deserialization/

https://github.com/sv3nbeast/ShiroScan

https://mp.weixin.qq.com/s/do88_4Td1CSeKLmFqhGCuQ