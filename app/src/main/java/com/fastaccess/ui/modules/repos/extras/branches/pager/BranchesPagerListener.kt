package com.fastaccess.ui.modules.repos.extras.branches.pager

import com.fastaccess.data.dao.BranchesModel

/**
 * Created by kosh on 15/07/2017.
 */
interface BranchesPagerListener {
    fun onItemSelect(branch: BranchesModel)
}