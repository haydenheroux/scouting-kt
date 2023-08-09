<#-- @ftlvariable name="event" type="io.github.haydenheroux.scouting.models.event.Event" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="${event.name}">
    <h1>${event.name}</h1>
    <hr/>
    <@layout.event_section event=event>
    </@layout.event_section>
</@layout.header>
