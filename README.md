converter_video_format
======================

Nesse trabalho fizemos um conversor de vídeos em formatos diferenets, ou seja, dado um arquivo em formato .dv, converteremos para o formato .mp4 com a finalidade de ser mostrado no browser depois do upload.

Para tanto, usamos o framework JSF do Java e a biblioteca Primefaces para o campo de upload. Em seguinda, criamos uma conta no Zencoder (zencoder.com) e utilizamos sua api, para, ao fazer o upload de um arquivo, uma cópia dele é salva em uma pasta temporária e uma requisição post é feita ao Zencoder, que retorna dados, os quais podemos pegar a url do arquivo mp4 convertida. Dessa maneira, gravamos o arquivo gerado usando a url supracitada na pasta temporária. 

Nós armazenamos os dois arquivos gerados no serviço Storage da Amazon S3 (https://aws.amazon.com/s3) e pegamos a url que está no Storage para mostrar o vídeo na tela do usuário.
