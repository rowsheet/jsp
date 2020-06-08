<pre>
<#list users as user>
SELECT
    *
FROM
    users
WHERE
    username = '${user.username}'
AND
    password = '${user.password}'; <br>
</#list>
</pre>
