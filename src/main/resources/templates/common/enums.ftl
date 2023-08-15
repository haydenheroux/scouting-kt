<#function region_to_text region>
    <#if region == "NEW_ENGLAND">
        <#return "New England">
    <#else>
        <#return "TODO: ${region}">
    </#if>
</#function>

<#function region_to_serial region>
    <#if region == "NEW_ENGLAND">
        <#return "ne">
    <#else>
        <#return "TODO: ${region}">
    </#if>
</#function>

<#function match_type_to_text match_type>
    <#if match_type == "QUALIFICATION">
        <#return "Qualification">
    <#elseif match_type == "QUARTER_FINAL">
        <#return "Quarters">
    <#elseif match_type == "SEMI_FINAL">
        <#return "Semis">
    <#elseif match_type == "FINAL">
        <#return "Finals">
    <#else>
        <#return "Match">
    </#if>
</#function>

<#function match_type_to_serial match_type>
    <#if match_type == "QUALIFICATION">
        <#return "qm">
    <#elseif match_type == "QUARTER_FINAL">
        <#return "qf">
    <#elseif match_type == "SEMI_FINAL">
        <#return "sf">
    <#elseif match_type == "FINAL">
        <#return "f">
    <#else>
        <#return "">
    </#if>
</#function>

<#macro alliance alliance>
    <#if alliance == "RED">
        <p class="red pill">Red Alliance</p>
    <#elseif alliance == "BLUE">
        <p class="blue pill">Blue Alliance</p>
    <#else>
        <p class="pill">No Alliance</p>
    </#if>
</#macro>
