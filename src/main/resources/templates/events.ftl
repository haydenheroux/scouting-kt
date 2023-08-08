<#-- @ftlvariable name="eventReferences" type="kotlin.collections.List<io.github.haydenheroux.scouting.models.event.EventReference>" -->
<#import "_layout.ftl" as layout />
<@layout.header title="Events">
    <h1>Events</h1>
    <hr/>
    <#list eventReferences?reverse as eventReference>
    <@layout.event_section event=eventReference>
    </@layout.event_section>
    </#list>
</@layout.header>
