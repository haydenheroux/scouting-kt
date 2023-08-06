<#-- @ftlvariable name="team" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <h2>${team.name} - ${team.number?c}</h2>
    <#list team.seasons as season>
        <a href="/teams/${team.number?c}/${season.year?c}">${season.year?c}</a>
    </#list>
</@layout.header>
