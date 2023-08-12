<#-- @ftlvariable name="event" type="io.github.haydenheroux.scouting.models.event.Event" -->
<#import "/common/layout.ftl" as layout />
<#import "/common/sections.ftl" as sections />
<@layout.header title="${event.name}">
    <h1>${event.name}</h1>
    <hr/>
    <@sections.event_section event=event />
</@layout.header>
