<html>
<body>
<pre>
    Hello FTL

    ##你看不到我
    <#--
    你看不到我
    -->
    ${value1}
    <#--${value2}-->
    <#--${value3}-->

    <#list colors as color>
        <tr><td>${color?index} : ${color?counter} : ${color}</td></tr>
    </#list>

    <#list map?keys as key>
        ${key} : ${map[key]}<br>
    </#list>


    ${user.name}
    ${user.getName()}

    <#assign title = "xiepuxin">
    <#include "header.ftl" parse = false><br/>
    <#include "header.ftl" parse = true><br/>

    <#macro test>
        Test text
    </#macro>
    <#-- call the macro: -->
    <@test/>

    <#macro render_color color index>
        Index : ${index} , Color : ${color}
    </#macro>

    <#list colors as color>
        <@render_color color color?index/>
    </#list>

    <#assign s1 = "${title} 1">
    <#--
        ${}不进行转义
        用${r'表达式'}的形式
    -->
    <#assign s2 = "${r'${title}'} 2">
    ${s1}<br/>
    ${s2}<br/>

    ${colors?size}<br/>
</pre>
</body>
</html>