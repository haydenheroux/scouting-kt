<#-- @ftlvariable name="events" type="kotlin.collections.List<io.github.haydenheroux.scouting.models.event.Event>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <#list events as event>
    <@layout.event_section event=event>
    </@layout.event_section>
    </#list>
</@layout.header>
