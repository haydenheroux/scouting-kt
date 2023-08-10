<#-- @ftlvariable name="team" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="Team ${team.number?c}">
    <h1><@layout.team_link team=team /></h1>
    <#list team.seasons?reverse as season>
        <@layout.season_section team=team season=season />
    </#list>
</@layout.header>
