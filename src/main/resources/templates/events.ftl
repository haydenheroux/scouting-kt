<#-- @ftlvariable name="events" type="kotlin.collections.List<io.github.haydenheroux.scouting.models.event.Event>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <h1>Events</h1>
    <hr/>
    <#list events as event>
    <@layout.event_section event=event>
    </@layout.event_section>
    </#list>
</@layout.header>
