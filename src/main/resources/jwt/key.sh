# 创建私钥
openssl ecparam -genkey -name prime256v1 -noout -out ec_private.pem
# 创建公钥
openssl ec -in ec_private.pem -pubout -out ec_public.pem
# 转换私钥
openssl pkcs8 -topk8 -inform PEM -outform DER -in ec_private.pem -nocrypt > ec_private_pkcs8
