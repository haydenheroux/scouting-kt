<#-- @ftlvariable name="events" type="kotlin.collections.List<io.github.haydenheroux.scouting.models.event.Event>" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="Events">
    <h1>Events</h1>
    <hr/>
    <#list events?reverse as event>
    <@layout.event_section event=event>
    </@layout.event_section>
    </#list>
</@layout.header>
