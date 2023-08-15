<#function region_to_text region>
    <#if region == "NEW_ENGLAND">
        <#return "New England">
    <#elseif region == "ISRAEL">
        <#return "Israel">
    <#elseif region == "MICHIGAN">
        <#return "Michigan">
    <#elseif region == "TEXAS">
        <#return "Texas">
    <#elseif region == "INDIANA">
        <#return "Indiana">
    <#elseif region == "CHESAPEAKE">
        <#return "Chesapeake">
    <#elseif region == "MID_ATLANTIC">
        <#return "Mid-Atlantic">
    <#elseif region == "PEACHTREE">
        <#return "Georgia">
    <#elseif region == "ONTARIO">
        <#return "Ontario">
    <#elseif region == "OTHER">
        <#return "Other">
    <#else>
        <#return "TODO: ${region}">
    </#if>
</#function>

<#function region_to_serial region>
    <#if region == "NEW_ENGLAND">
        <#return "ne">
    <#elseif region == "ISRAEL">
        <#return "isr">
    <#elseif region == "MICHIGAN">
        <#return "fim">
    <#elseif region == "TEXAS">
        <#return "fit">
    <#elseif region == "INDIANA">
        <#return "fin">
    <#elseif region == "CHESAPEAKE">
        <#return "chs">
    <#elseif region == "MID_ATLANTIC">
        <#return "fma">
    <#elseif region == "PEACHTREE">
        <#return "pch">
    <#elseif region == "ONTARIO">
        <#return "ont">
    <#elseif region == "OTHER">
        <#return "other">
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
