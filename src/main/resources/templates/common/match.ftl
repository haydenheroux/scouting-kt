<#import "/common/enums.ftl" as enums />

<#function match_to_text match>
    <#return "${enums.match_type_to_text(match.type)} ${set_to_text(match)} Match ${match.number?c}">
</#function>

<#function match_to_text_short match>
    <#return "${enums.match_type_to_text(match.type)} ${match.number}">
</#function>

<#function set_to_text match>
    <#if match.type != "QUALIFICATION">
        <#return "${match.set?c}">
    <#else>
        <#return "">
    </#if>
</#function>

<#function match_to_serial match>
    <#return "${enums.match_type_to_serial(match.type)}${set_to_serial(match)}${match.number?c}">
</#function>

<#function set_to_serial match>
    <#if match.type != "QUALIFICATION">
        <#return "${match.set?c}m">
    <#else>
        <#return "">
    </#if>
</#function>
