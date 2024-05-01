/*
package com.tekcapzule.mediaconverter.application.mapper;

import com.tekcapzule.campaign.domain.command.CreateLeadCommand;
import com.tekcapzule.core.domain.Command;
import com.tekcapzule.core.domain.ExecBy;
import com.tekcapzule.core.domain.Origin;
import com.tekcapzule.campaign.domain.command.CreateCampaignCommand;
import com.tekcapzule.campaign.domain.command.UpdateCampaignCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.BiFunction;

@Slf4j
public final class InputOutputMapper {
    private InputOutputMapper() {

    }

    public static final BiFunction<Command, Origin, Command> addOrigin = (command, origin) -> {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        command.setChannel(origin.getChannel());
        command.setExecBy(ExecBy.builder().tenantId(origin.getTenantId()).userId(origin.getUserId()).build());
        command.setExecOn(utc.toString());
        return command;
    };

    public static final BiFunction<CreateCampaignInput, Origin, CreateCampaignCommand> buildCreateCampaignCommandFromCreateCampaignInput = (createInput, origin) -> {
        CreateCampaignCommand createCommand =  CreateCampaignCommand.builder().build();
        BeanUtils.copyProperties(createInput, createCommand);
        addOrigin.apply(createCommand, origin);
        return createCommand;
    };

    public static final BiFunction<UpdateCampaignInput, Origin, UpdateCampaignCommand> buildUpdateCampaignCommandFromUpdateCampaignInput = (updateInput, origin) -> {
        UpdateCampaignCommand updateCommand = UpdateCampaignCommand.builder().build();
        BeanUtils.copyProperties(updateInput, updateCommand);
        addOrigin.apply(updateCommand, origin);
        return updateCommand;
    };
    public static final BiFunction<CreateLeadInput, Origin, CreateLeadCommand> buildCreateLeadCommandFromCreateLeadInput = (createInput, origin) -> {
        CreateLeadCommand createLeadCommand =  CreateLeadCommand.builder().build();
        BeanUtils.copyProperties(createInput, createLeadCommand);
        addOrigin.apply(createLeadCommand, origin);
        return createLeadCommand;
    };
}
*/
