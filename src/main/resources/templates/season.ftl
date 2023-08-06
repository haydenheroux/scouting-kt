<#-- @ftlvariable name="season" type="io.github.haydenheroux.scouting.models.team.Season" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <h2>${season.year?c}</h2>
    <#list season.events as event>
    <@layout.event_section event=event>
    </@layout.event_section>
    </#list>
</@layout.header>
