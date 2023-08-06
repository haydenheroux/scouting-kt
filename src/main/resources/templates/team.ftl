<#-- @ftlvariable name="team" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <h1>${team.name} - ${team.number?c}</h1>
    <#list team.seasons as season>
        <@layout.season_section season=season>
        </@layout.season_section>
    </#list>
</@layout.header>
