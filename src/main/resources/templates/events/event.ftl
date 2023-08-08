<#-- @ftlvariable name="eventReference" type="io.github.haydenheroux.scouting.models.event.EventReference" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="${eventReference.name}">
    <h1>${eventReference.name}</h1>
    <hr/>
    <@layout.event_section event=eventReference>
    </@layout.event_section>
</@layout.header>
