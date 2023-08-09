<#-- @ftlvariable name="team" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="Team ${team.number?c}">
    <@layout.team_link team=team size="large" />
    <#list team.seasons?reverse as season>
        <@layout.season_section season=season />
    </#list>
</@layout.header>
